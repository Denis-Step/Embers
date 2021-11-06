import {useEffect, useMemo, useState} from "react";
import {Transaction} from "./types";
import {useTable} from "react-table";
import {useAuth} from "../contexts/cognitoAuthContext";
import {getTransactions} from "./apicalls";

// eslint-disable-next-line react-hooks/rules-of-hooks
export const useHeadlessTransactionsTable = (transactions: Transaction[]) => {

    // eslint-disable-next-line react-hooks/rules-of-hooks
    const transactionColumns = useMemo(() => [
        {
            Header: 'Description',
            accessor: 'description'
        },
        {
            Header: 'Date',
            accessor: 'date'
        },
        {
            Header: 'Amount',
            accessor: 'amount'
        },
        {
            Header: 'Merchant Name',
            accessor: 'merchantName',
        },
        {
            Header: 'Transaction ID',
            accessor: 'transactionId'
        }
    ] as any[], [])

// eslint-disable-next-line react-hooks/rules-of-hooks
    const testData = useMemo( () => transactions
        , [transactions])

    // eslint-disable-next-line react-hooks/rules-of-hooks
    return useTable({ columns: transactionColumns,
        data: testData})
}

// Dependency on useAuth.
export const useTransactions = (startDate: Date) => {
    const user = "Denny"
    const token = useAuth().token.id_token;
    const [transactions, setTransactions] = useState<Transaction[]>([]);

    useEffect(() => {

        const refreshTransactions = async () => {
            const transactions = await getTransactions(user, startDate, token);
            setTransactions(transactions);
        }

        refreshTransactions();

    }, [startDate])

    return transactions;

}