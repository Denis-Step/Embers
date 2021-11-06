import React, {useEffect, useState} from "react";
import {TransactionsTable} from "../components/tables/TransactionsTable";
import {TopNavBar} from "../components/navigation/Nav";
import {ViewTransactions} from "../components/transactions/ViewTransactions";

const testTransactions = [
    {
        "user": "Test",
        "institutionName": "Institution",
        "accountId": "Test Account",
        "description": "Test",
        "date": "2021-01-01",
        "amount": 5.00,
        "merchantName": "Merchant Name",
        "transactionId": "transactionId"}
]

export const ViewTransactionsPage = () => {

    return (
        <>
            <TopNavBar />
            <ViewTransactions />
        </>
    )
}