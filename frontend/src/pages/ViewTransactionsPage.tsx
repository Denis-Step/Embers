import React, {useEffect, useState} from "react";
import {TransactionsTable} from "../components/tables/TransactionsTable";
import {getTransactions} from "../common/apicalls";
import {useAuth} from "../contexts/cognitoAuthContext";
import {Transaction} from "../common/types";

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
    const auth = useAuth();
    const [sampleTx, setSampleTx] = useState<Transaction[]>();

    useEffect( () => {
        if (!sampleTx) {
            getTransactions("Denny", "2021-10-01", auth.token.id_token).then(
                (newTx) => setSampleTx(newTx))
        }
    }, [])


    return (
        <TransactionsTable transactions={sampleTx || [] } />
    )
}