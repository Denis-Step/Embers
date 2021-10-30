import {useMemo} from "react";
import {Transaction} from "./types";
import {useTable} from "react-table";

// eslint-disable-next-line react-hooks/rules-of-hooks
export const useTransactionsTable = (transactions: Transaction[]) => {

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