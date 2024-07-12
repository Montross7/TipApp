package com.example.jettip

import android.content.res.Resources.Theme
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jettip.components.InputField
import com.example.jettip.ui.theme.JetTipTheme
import com.example.jettip.utils.calculateTotalPerPerson
import com.example.jettip.utils.calculateTotalTip
import com.example.jettip.widgets.RoundIconButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp {
                TopHeader(123.0)
            }
        }
    }
}

@Composable
fun MyApp(content: @Composable () -> Unit) {
    JetTipTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            content()
        }
    }
}

//@Preview
@Composable
fun TopHeader(totalPerPerson: Double = 0.0) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(
                150.dp
            )
            .padding(16.dp)
            .clip(shape = RoundedCornerShape(corner = CornerSize(12.dp))),
        color = Color(color = 0xFFE9D7F7)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val total = "%.2f".format(totalPerPerson)
            Text(text = "Total per Person", style = MaterialTheme.typography.headlineSmall)
            Text(
                text = "$$total",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Preview
@Composable
fun MainContent() {

    val range = 1.0f.. 100.0f

    val splitByState = remember {
        mutableStateOf(1)
    }
    val tipAmountState = remember {
        mutableStateOf(0.0)
    }

    val totalPerPersonState = remember {
        mutableStateOf(0.0)
    }

    BillForm(
        range = range,
        splitByState = splitByState,
        tipAmountState = tipAmountState,
        totalPerPersonState = totalPerPersonState
    ) { }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BillForm(
    range: ClosedFloatingPointRange<Float> = 1.0f..100.0f,
    splitByState: MutableState<Int>,
    tipAmountState: MutableState<Double>,
    totalPerPersonState: MutableState<Double>,
    modifier: Modifier = Modifier, onValChanged: (String) -> Unit = {}
) {
    val totalBillState = remember {
        mutableStateOf("")
    }

    val tipPercentageState = remember {
        mutableStateOf(33.0f)
    }

    val validState = remember(totalBillState.value) {
        totalBillState.value.trim().isNotEmpty()
    }

    val keyboardController = LocalSoftwareKeyboardController.current

    fun updateTip() {
        tipAmountState.value =
            calculateTotalTip(
                totalBill = totalBillState.value.toDouble(),
                tipPercentage = tipPercentageState.value
            )
    }

    fun updateTotalPerPerson() {
        totalPerPersonState.value = calculateTotalPerPerson(
            totalBill = totalBillState.value.toDouble(),
            tipPercentage = tipPercentageState.value,
            splitBy = splitByState.value
        )
    }

    Column {
        TopHeader(totalPerPersonState.value)
        Surface(
            modifier = modifier
                .padding(2.dp)
                .fillMaxWidth(),
            shape = CircleShape.copy(CornerSize(8.dp)),
            border = BorderStroke(width = 1.dp, Color.LightGray)
        ) {
            Column(
                modifier = Modifier.padding(all = 6.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                InputField(valueState = totalBillState,
                    labelId = "Enter Bill",
                    enabled = true,
                    isSingleLine = true,
                    onAction = KeyboardActions {
                        if (!validState) return@KeyboardActions

                        onValChanged(totalBillState.value.trim())
                        updateTip()
                        updateTotalPerPerson()

                        keyboardController?.hide()
                    })
                if (validState) {
                    Row(
                        modifier = Modifier.padding(3.dp),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            text = "Split",
                            modifier = Modifier.align(alignment = Alignment.CenterVertically)
                        )
                        Spacer(modifier = Modifier.width(120.dp))
                        Row(
                            modifier = Modifier.padding(horizontal = 3.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            RoundIconButton(imageVector = Icons.Default.Remove) {
                                if (splitByState.value > 1) {
                                    splitByState.value -= 1
                                    updateTotalPerPerson()
                                }
                            }
                            Text(
                                text = "${splitByState.value}",
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .padding(start = 9.dp, end = 9.dp)
                            )
                            RoundIconButton(imageVector = Icons.Default.Add) {
                                splitByState.value += 1
                                updateTotalPerPerson()
                            }
                        }

                    }
                    Row(
                        modifier = Modifier.padding(horizontal = 3.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(text = "Tip", modifier = Modifier.align(Alignment.CenterVertically))
                        Spacer(modifier = Modifier.width(200.dp))
                        Text(
                            text = "$" + "%.2f".format(tipAmountState.value),
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "%.2f".format(tipPercentageState.value) + "%")
                        Spacer(modifier = Modifier.height(14.dp))
                        Slider(
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                            value = tipPercentageState.value,
                            onValueChange = { newVal ->
                                tipPercentageState.value = newVal
                                updateTip()
                                updateTotalPerPerson()
                            },
                            steps = 5,
                            valueRange = range
                        )
                    }
                } else {
                    Box() {}
                }

            }
        }
    }
}


@Preview
@Composable
fun PreviewView() {
    JetTipTheme {
        // A surface container using the 'background' color from the theme
        MyApp {
            BillForm()
        }
    }
}
