package com.example.jettip.utils

fun calculateTotalTip(totalBill: Double, tipPercentage: Float): Double {
    return if (totalBill > 1 && totalBill.toString().isNotEmpty()) (totalBill * tipPercentage) / 100
    else
        0.0
}

fun calculateTotalPerPerson (totalBill: Double, splitBy: Int, tipPercentage: Float): Double{
    val bill = calculateTotalTip(totalBill,tipPercentage) + totalBill
    return (bill)/splitBy
}
