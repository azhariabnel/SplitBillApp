@file:OptIn(ExperimentalComposeUiApi::class)

package com.example.splitbillapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.splitbillapp.components.InputField
import com.example.splitbillapp.ui.theme.SplitBillAppTheme
import com.example.splitbillapp.utils.calculateTotalPerPerson
import com.example.splitbillapp.utils.calculateTotalTip
import com.example.splitbillapp.widgets.RoundIconButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SplitBillAppTheme {
                // A surface container using the 'background' color from the theme
                SbApp {
                    MainContent()
                }
            }
        }
    }
}

@Composable
fun SbApp(content: @Composable () -> Unit){
    Surface(
        color = MaterialTheme.colors.background
    ) {
        content()
    }
}

@Composable
fun TopHeader(totalPerPerson : Int = 0){
    Surface(modifier = Modifier
        .padding(5.dp)
        .fillMaxWidth()
        .height(150.dp)
        .clip(shape = RoundedCornerShape(corner = CornerSize(12.dp))),
        color = Color.DarkGray
    ) {
        Column (
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
                ) {
            Text(text = "Total Per Person", style = MaterialTheme.typography.h5, color = Color.White)
            Text(text = "IDR $totalPerPerson", style = MaterialTheme.typography.h4, color = Color.White, fontWeight = FontWeight.ExtraBold)
        }
    }
}



@Composable
fun MainContent(){
    val totalSplitState = remember {
        mutableStateOf(1)
    }
    val tipAmountState = remember {
        mutableStateOf(0)
    }
    val totalPerPersonState = remember {
        mutableStateOf(0)
    }
    Column(modifier = Modifier.padding(all = 5.dp)) {
        BillsForm(totalSplitState = totalSplitState, tipAmountState = tipAmountState, totalPerPersonState = totalPerPersonState){

        }
    }
}


@Composable
fun BillsForm(modifier: Modifier = Modifier,
              splitRange: IntRange = 1..100,
              totalSplitState: MutableState<Int>,
              tipAmountState: MutableState<Int>,
              totalPerPersonState: MutableState<Int>,
                        onValChange: (String) -> Unit = {}) {

    val totalBillsState = remember {
        mutableStateOf("")
    }
    val validState = remember(totalBillsState.value) {
        totalBillsState.value.trim().isNotEmpty()
    }
    val sliderPositionState = remember {
        mutableStateOf(0f)
    }
    val tipPercentage = (sliderPositionState.value * 100).toInt()
    val keyboardControl = LocalSoftwareKeyboardController.current


    TopHeader(totalPerPerson = totalPerPersonState.value)

    Surface(
        modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(corner = CornerSize(8.dp)),
        border = BorderStroke(width = 2.dp, color = Color.Black)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            InputField(valueState = totalBillsState,
                labelId = "Input Bill",
                enabled = true,
                isSingleLine = true,
                onAction = KeyboardActions {
                    if (!validState) return@KeyboardActions
                    onValChange(totalBillsState.value.trim())
                    keyboardControl?.hide()
                    totalPerPersonState.value =
                        calculateTotalPerPerson(totalBill = totalBillsState.value.toInt(), splitBy = totalSplitState.value
                            , tipPercentage = tipPercentage)
                })
            if (validState){
            Row(
                modifier = Modifier.padding(10.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = "Split",
                    modifier = Modifier.align(
                        alignment = Alignment.CenterVertically
                    )
                )
                Spacer(modifier = Modifier.width(75.dp))
                Row(
                    modifier = Modifier.padding(horizontal = 5.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    RoundIconButton(imageVector = Icons.Default.Remove,
                        onClick = {
                            totalSplitState.value =
                                if (totalSplitState.value > 1) totalSplitState.value -1
                            else 1
                            totalPerPersonState.value =
                                calculateTotalPerPerson(totalBill = totalBillsState.value.toInt(), splitBy = totalSplitState.value
                                    , tipPercentage = tipPercentage)
                        })
                    Text(
                        text = "${totalSplitState.value} Person",
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = 10.dp, end = 10.dp)
                    )
                    RoundIconButton(imageVector = Icons.Default.Add,
                        onClick = {
                            if (totalSplitState.value < splitRange.last){
                                totalSplitState.value = totalSplitState.value + 1
                                totalPerPersonState.value =
                                    calculateTotalPerPerson(totalBill = totalBillsState.value.toInt(), splitBy = totalSplitState.value
                                        , tipPercentage = tipPercentage)
                            }

                        })
                }
            }
            Row(
                modifier = Modifier.padding(10.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = "Tip", modifier = Modifier.align(
                        alignment = Alignment.CenterVertically
                    )
                )
                Spacer(modifier = Modifier.width(200.dp))
                Text(
                    text = "IDR ${tipAmountState.value}", modifier = Modifier.align(
                        alignment = Alignment.CenterVertically
                    )
                )
            }
            Column(
                modifier = Modifier.padding(10.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Tip Percentage $tipPercentage%")

                Spacer(modifier = Modifier.height(5.dp))

                Slider(
                    value = sliderPositionState.value,
                    onValueChange = {
                        sliderPositionState.value = it
                        tipAmountState.value =
                            calculateTotalTip(totalBill = totalBillsState.value.toInt(), tipPercentage = tipPercentage)
                        totalPerPersonState.value =
                            calculateTotalPerPerson(totalBill = totalBillsState.value.toInt(), splitBy = totalSplitState.value
                            , tipPercentage = tipPercentage)
                    },
                    steps = 4
                )
            }
            } else {
                Box(){

                }
            }
        }

    }
}




@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SplitBillAppTheme {
        SbApp {
            MainContent()
        }
    }
}