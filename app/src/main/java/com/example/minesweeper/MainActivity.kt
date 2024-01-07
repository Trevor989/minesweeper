package com.example.minesweeper

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.minesweeper.ui.theme.MinesweeperTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MinesweeperTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Minesweeper()
                }
            }
        }
    }
}

fun ShouldSet(GridPosition: Int,NumberOfColumn: Int,BombPosition: Int,r3: Int,EachGridPoint: Int): Boolean {
    return Math.pow(Math.pow(((GridPosition / NumberOfColumn - BombPosition / NumberOfColumn).toDouble()),2.0).toInt() + Math.pow(((GridPosition % NumberOfColumn - BombPosition % NumberOfColumn).toDouble()),2.0), 0.5) <= r3 && EachGridPoint < 4-r3
}

fun GridColor(IsGridVisible: Boolean): Color {
    return if (IsGridVisible) {
        Color(236, 236, 236, 255)
    } else {
        Color(193,193,193,255)
    }
}

fun PointColor(EachGridPoint: Int): Color {
    return when (EachGridPoint) {
        1 -> Color(222, 60, 44, 255)
        2 -> Color(241, 187, 62, 255)
        3 -> Color(98, 72, 144, 255)
        else -> Color(0, 0, 0, 255)
    }
}

fun Description(IsEndGame: Boolean,FirstPlayerScore: Int,SecondPlayerScore: Int,IsFirstPlayerTurn: Boolean): String{
    if (IsEndGame) {
        if (FirstPlayerScore>SecondPlayerScore) {
            return "First player win"
        }
        else if (FirstPlayerScore<SecondPlayerScore) {
            return "Second player win"
        }
        else {
            return "Draw"
        }
    }
    else {
        if (IsFirstPlayerTurn) {
            return "First player turn"
        }
        else {
            return "Second player turn"
        }
    }
}

fun ImageVisible(IsGridVisible: Boolean,EachGridPoint2: Int): Float {
    if (IsGridVisible && EachGridPoint2 == -10) {
        return 1.toFloat()
    }
    return 0.toFloat()
}

fun PointVisible(IsGridVisible: Boolean,EachGridPoint2: Int): Float {
    if (IsGridVisible && EachGridPoint2 != 0) {
        return 1.toFloat()
    }
    return 0.toFloat()
}

fun DescriptionColor(IsFirstPlayerTurn: Boolean): Color {
    if (IsFirstPlayerTurn) {
        return Color(200, 78, 79, 255)
    }
    return Color(79,135,83,255)
}

@Composable
fun Minesweeper(modifier: Modifier = Modifier) {
    var FirstPlayerScore by remember { mutableIntStateOf(0) }
    var SecondPlayerScore by remember { mutableIntStateOf(0) }
    var NumberOfBombs by remember { mutableIntStateOf(5) }
    var IsFirstPlayerTurn by remember { mutableStateOf(true) }
    var GameState by remember { mutableStateOf("NeverCreate") }
    var IsEndGame by remember { mutableStateOf(false) }
    var IsReseting by remember { mutableStateOf(false) }
    var GridPoint by remember { mutableStateOf(listOf<Int>()) }
    var BombPositions by remember { mutableStateOf(listOf<Int>()) }
    var NumberOfRaw by remember { mutableIntStateOf(10) }
    var NumberOfColumn by remember { mutableIntStateOf(6) }
    var EachGridPoint by remember { mutableIntStateOf(0) }
    if (GameState!="FinishedPreparing") {
        for (GridPosition in (0..NumberOfRaw * NumberOfColumn - 1)) {
            GridPoint = GridPoint.toMutableList().apply {
                if (GameState == "NeverCreate") {
                    add(0)
                } else {
                    set(GridPosition, 0)
                }
            }
        }
        BombPositions = (0..NumberOfRaw * NumberOfColumn - 1).shuffled().take(NumberOfBombs).sorted()
        for (BombPosition in BombPositions) {
            GridPoint = GridPoint.toMutableList().apply {
                set(BombPosition, -10)
            }
        }
        for (GridPosition in (0..NumberOfRaw * NumberOfColumn - 1)) {
            for (BombPosition in BombPositions) {
                GridPoint = GridPoint.toMutableList().apply {
                    if (GridPoint[GridPosition] != -10) {
                        for (Distance in (1..3)) {
                            EachGridPoint=4-Distance
                            if (ShouldSet(GridPosition,NumberOfColumn,BombPosition,Distance,GridPoint[GridPosition])) {
                                set(GridPosition, EachGridPoint)
                                break
                            }
                        }
                    }
                }
            }
        }
        GameState="FinishedPreparing"
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(236, 236, 236, 255)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.padding(top = 10.dp)
        ) {
            Row(
                modifier = Modifier.padding(end = 10.dp)
                    .width(30.dp)
            ) {
                Text(
                    text = FirstPlayerScore.toString(),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(200, 78, 79, 255),
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = "Minesweeper",
                modifier = Modifier,
                color = Color(50,50,50,255),
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier.padding(start = 10.dp)
                    .width(30.dp)
            ) {
                Text(
                    text = SecondPlayerScore.toString(),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(79,135,83,255),
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Text(
            text = Description(IsEndGame,FirstPlayerScore,SecondPlayerScore,IsFirstPlayerTurn),
            modifier = Modifier.padding(top = 10.dp),
            color = DescriptionColor(IsFirstPlayerTurn),
            fontWeight = FontWeight.Bold
        )
        Column(
            modifier = Modifier.padding(top = 30.dp)
        ) {
            LazyColumn {
                items(NumberOfRaw) { GridColumn ->
                    LazyRow {
                        items(NumberOfColumn) { GridRow ->
                            var IsGridVisible by remember { mutableStateOf(false) }
                            var EachGridPoint2=GridPoint[GridColumn * NumberOfColumn + GridRow]
                            if (IsReseting) {
                                IsGridVisible = false
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .padding(all = 2.dp)
                                    .background(GridColor(IsGridVisible))
                                    .size(60.dp, 60.dp)
                                    .clickable {
                                        GridPoint = GridPoint.toMutableList().apply {
                                                if (IsReseting) {
                                                    IsReseting=false
                                                }
                                                if (!IsEndGame) {
                                                    if (EachGridPoint2 == -10) {
                                                        IsEndGame=true
                                                    }
                                                    if (!IsGridVisible) {
                                                        if (IsFirstPlayerTurn) {
                                                            FirstPlayerScore+=EachGridPoint2
                                                        }
                                                        else {
                                                            SecondPlayerScore+=EachGridPoint2
                                                        }
                                                        IsFirstPlayerTurn=!IsFirstPlayerTurn
                                                        IsGridVisible = true
                                                    }
                                                }
                                            }
                                        }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.im1),
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize()
                                            .alpha(ImageVisible(IsGridVisible,EachGridPoint2)),
                                        contentScale = ContentScale.Crop
                                    )
                                    Text(
                                        text = EachGridPoint2.toString(),
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        color = PointColor(EachGridPoint2),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .alpha(PointVisible(IsGridVisible,EachGridPoint2))
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 15.dp)
                .size(200.dp,40.dp)
                .alpha((IsEndGame.compareTo(false)).toFloat())
                .background(Color(98,72,144,255))
                .clickable {
                    GridPoint = GridPoint.toMutableList().apply {
                        if (IsEndGame) {
                            IsEndGame=false
                            FirstPlayerScore=0
                            SecondPlayerScore=0
                            GameState="Preparing"
                            IsFirstPlayerTurn=true
                            IsReseting=true
                        }
                    }
                }
        ) {
            Text(
                text = "Play again",
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth(),
                color = Color(236, 236, 236, 255)
            )
        }
    }
}
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MinesweeperTheme {
        Minesweeper()
    }
}