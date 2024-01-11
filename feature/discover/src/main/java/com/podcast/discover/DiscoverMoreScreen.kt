package com.podcast.discover

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import caios.android.kanade.core.design.R
import caios.android.kanade.core.model.podcast.EntryItem
import caios.android.kanade.core.model.podcast.toFeedModel
import caios.android.kanade.core.ui.AsyncLoadContents
import caios.android.kanade.core.ui.TrackScreenViewEvent
import caios.android.kanade.core.ui.collectAsStateLifecycleAware
import caios.android.kanade.core.ui.dialog.showAsButtonSheet
import caios.android.kanade.core.ui.music.FeedPodcastHolder
import caios.android.kanade.core.ui.view.DropDownMenuItemData
import caios.android.kanade.core.ui.view.FixedWithEdgeSpace
import caios.android.kanade.core.ui.view.KanadeTopAppBar
import caios.android.kanade.core.ui.view.itemsWithEdgeSpace
import findActivity
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import java.util.Locale

@Composable
internal fun DiscoverMoreRouter(
    modifier: Modifier = Modifier,
    onClickPodcast: (String) -> Unit,
    onTerminate: () -> Unit,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    viewModel: DiscoverViewModel = hiltViewModel(),
) {
    DiscoverMoreScreen(
        modifier, onClickPodcast, onTerminate, contentPadding, viewModel
    )

    TrackScreenViewEvent("DiscoverMoreScreen")
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun DiscoverMoreScreen(
    modifier: Modifier = Modifier,
    onClickPodcast: (String) -> Unit,
    onTerminate: () -> Unit,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    viewModel: DiscoverViewModel
) {
    val state = rememberTopAppBarState()
    val behavior = TopAppBarDefaults.pinnedScrollBehavior(state)
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateLifecycleAware()
    Scaffold(
        modifier = modifier.nestedScroll(behavior.nestedScrollConnection),
        topBar = {
            KanadeTopAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(id = R.string.discover),
                behavior = behavior,
                onTerminate = onTerminate,
                dropDownMenuItems = persistentListOf(
                    DropDownMenuItemData(
                        text = R.string.select_country,
                        onClick = {
                            context.findActivity()?.showAsButtonSheet(
                                userData = null,
                                skipPartiallyExpanded = false
                            ) { onDismiss ->
                                SelectCountry(
                                    currentCountryCode = viewModel.selectCountryCode,
                                    selectCountry = {
                                        viewModel.saveCountryCode(it)
                                        onDismiss.invoke()
                                    },
                                    dismissDialog = {
                                        onDismiss.invoke()
                                    }
                                )
                            }
                        },
                    )
                ),
            )
        },
    ) { paddingValues ->
        AsyncLoadContents(
            modifier = modifier,
            screenState = uiState,
        ) { discoverUiState ->
            LazyVerticalGrid(
                modifier = Modifier.padding(top = paddingValues.calculateTopPadding()),
                contentPadding = contentPadding,
                columns = FixedWithEdgeSpace(
                    count = 3,
                    edgeSpace = 8.dp,
                ),
            ) {
                itemsWithEdgeSpace(
                    spanCount = 3,
                    items = discoverUiState.items,
                    key = { artist -> "added-${artist.id}" },
                ) { artist ->
                    FeedPodcastHolder(
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateItemPlacement(),
                        feed = artist.toFeedModel(),
                        onClickHolder = {
                            artist.id?.attributes?.imId?.let {
                                onClickPodcast.invoke(
                                    it
                                )
                            }
                        },
                    )
                }
            }
        }

    }
}

@Composable
private fun SelectCountry(
    currentCountryCode: String,
    selectCountry: (String) -> Unit,
    dismissDialog: () -> Unit
) {
    val countryCodeArray: List<String> = Locale.getISOCountries().asList()
    val countryCodeNames: MutableMap<String, String> = HashMap()
    val countryNameCodes: MutableMap<String, String> = HashMap()
    for (code in countryCodeArray) {
        val locale = Locale("", code)
        val countryName = locale.displayCountry
        countryCodeNames[code] = countryName
        countryNameCodes[countryName] = code
    }

    val countryNamesSort: List<String> = ArrayList(countryCodeNames.values).sorted()
    val state = rememberLazyListState()

    Surface {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .padding(
                        horizontal = 16.dp,
                        vertical = 16.dp
                    )
            ) {
                Text(
                    text = stringResource(id = R.string.select_country),
                    modifier = Modifier
                        .weight(1f),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                IconButton(
                    onClick = {
                        dismissDialog.invoke()
                    }
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "close")
                }
            }

            LazyColumn(
                state = state,
                content = {
                    items(countryNamesSort) {
                        val backgroundColor = if (currentCountryCode == countryNameCodes[it])
                            MaterialTheme.colorScheme.surfaceContainerHigh
                        else Color.Transparent
                        Box(
                            modifier = Modifier
                                .background(
                                    backgroundColor,
                                )
                                .fillMaxWidth()
                                .clickable {
                                    val countryName = it
                                    if (countryNameCodes.containsKey(countryName)) {
                                        val countryCode =
                                            countryNameCodes[countryName] ?: return@clickable
                                        selectCountry.invoke(countryCode)
                                    }

                                }
                        ) {
                            Text(
                                text = it,
                                modifier = Modifier
                                    .padding(
                                        horizontal = 16.dp,
                                        vertical = 12.dp
                                    )
                                    .fillMaxWidth()
                            )
                        }
                    }
                })
        }
    }
    LaunchedEffect(key1 = Unit, block = {
        state.animateScrollToItem(
            countryCodeNames.keys.sorted().indexOf(currentCountryCode),
            scrollOffset = 50000
        )
    })
}
