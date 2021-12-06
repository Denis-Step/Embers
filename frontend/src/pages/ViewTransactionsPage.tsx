import React, {useEffect, useState} from "react";
import {TopNavBar} from "../components/navigation/Nav";
import {ViewTransactions} from "../components/transactions/ViewTransactions";


export const ViewTransactionsPage = () => {

    return (
        <>
            <TopNavBar />
            <ViewTransactions />
        </>
    )
}