package com.moaapps.customnavbar

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moaapps.customnavbar.ui.theme.CustomNavbarTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {




    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val navItemsList = listOf(
                LightItem(title = "Home", icon = rememberVectorPainter(image = Icons.Outlined.Home)),
                LightItem(title = "Bookmarks", icon = painterResource(id = R.drawable.ic_bookmark)),
                LightItem(title = "Add", icon = rememberVectorPainter(image = Icons.Outlined.AddCircle)),
                LightItem(title = "Profile", icon = rememberVectorPainter(image = Icons.Outlined.Person)),
                LightItem(title = "Settings", icon = rememberVectorPainter(image = Icons.Outlined.Settings)),
            )

            CustomNavbarTheme {
                Scaffold(
                    bottomBar = {
                        LightNavBar(
                            items = navItemsList,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .background(
                                    Color.Black
                                )
                        )
                    }
                ){

                }

            }
        }
    }
}


@Composable
fun LightNavBar(
    modifier: Modifier = Modifier,
    items: List<LightItem>,
    onItemSelected: (Int, LightItem) -> Unit = {_, _ ->},
    selectedItem: Int = 0

){

    val indicatorPosition = remember {
        Animatable(0f)
    }


    val tabsPosition = mutableMapOf<Int, Float>()

    var selectedTab by remember {
        mutableIntStateOf(selectedItem)
    }

    var clickedTab by remember {
        mutableIntStateOf(selectedItem)
    }


    LaunchedEffect(key1 = clickedTab) {
        val tabPosition = tabsPosition[clickedTab]!!
        indicatorPosition.animateTo(
            tabPosition,
            animationSpec = androidx.compose.animation.core.tween(
                durationMillis = 500
            )
        )

        selectedTab = clickedTab
        onItemSelected(clickedTab, items[clickedTab])
    }


    Box(
        modifier = modifier,
    ){

        Row(
            modifier = Modifier
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically

        ){


            for (i in items.indices) {
                val navBarItem = items[i]

                Column(

                    modifier = Modifier
                        .weight(1f)
                        .clickable(
                            indication = null,
                            interactionSource = remember {
                                MutableInteractionSource()
                            }
                        ) {
                            clickedTab = i
                        }
                        .onGloballyPositioned { coordinates ->
                            tabsPosition[i] = coordinates.positionInWindow().x
                        },

                    horizontalAlignment = Alignment.CenterHorizontally

                ) {
                    Icon(
                        painter = navBarItem.icon,
                        contentDescription = navBarItem.title,
                        modifier = Modifier.size(30.dp),
                        tint = if (selectedTab == i) Color.White else Color.DarkGray
                    )

                    if (navBarItem.title.isNotEmpty()){
                        Text(
                            text = navBarItem.title,
                            fontSize = 12.sp,
                            color = if (selectedTab == i) Color.White else Color.DarkGray,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }


                }



            }


        }


        NavBarIndicator(
            modifier = Modifier
                .fillMaxWidth(1f / items.size)
                .align(Alignment.CenterStart)
                .graphicsLayer {
                    translationX = indicatorPosition.value
                }
        )
    }

}


@Composable
fun NavBarIndicator(
    modifier: Modifier
){

    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){

        Canvas(
            modifier =
            Modifier.fillMaxSize()

        ) {

            val mPath = Path().apply {
                reset()
                moveTo(size.width.times(0.35f), 0f)
                lineTo(size.width.times(0.65f), 0f)
                lineTo(size.width.times(0.8f), size.height)
                lineTo(size.width.times(0.2f), size.height)
                close()
            }



            drawPath(
                path = mPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.25f),
                        Color.White.copy(alpha = 0.0f)
                    )
                ),
                style = Fill
            )

            drawPath(
                path = Path().apply {
                    moveTo(size.width.times(0.3f), 0f)
                    lineTo(size.width.times(0.7f), 0f)
                },
                color = Color.White,
                style = Stroke(
                    width = 25f,
                    cap = StrokeCap.Round
                )
            )
        }


    }


}


@Preview(showBackground = false)
@Composable
fun LightNavbarPreview() {
    CustomNavbarTheme {


        LightNavBar(
            items = listOf(
                LightItem(title = "", icon = rememberVectorPainter(image = Icons.Outlined.Home)),
                LightItem(title = "", icon = painterResource(id = R.drawable.ic_bookmark)),
                LightItem(title = "", icon = rememberVectorPainter(image = Icons.Outlined.AddCircle)),
                LightItem(title = "", icon = rememberVectorPainter(image = Icons.Outlined.Person)),
                LightItem(title = "", icon = rememberVectorPainter(image = Icons.Outlined.Settings)),
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(
                    Color.Black
                )
        )

    }
}