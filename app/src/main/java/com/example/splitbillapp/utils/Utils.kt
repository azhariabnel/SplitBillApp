package com.example.splitbillapp.utils

fun calculateTotalTip(totalBill: Int, tipPercentage: Int): Int {
    return if (totalBill > 1 && totalBill.toString().isNotEmpty())
        (totalBill * tipPercentage) / 100 else 0
}

fun calculateTotalPerPerson(
    totalBill: Int,
    splitBy: Int,
    tipPercentage: Int,
): Int{
    val bill = calculateTotalTip(totalBill = totalBill, tipPercentage = tipPercentage) + totalBill

    return (bill / splitBy)
}