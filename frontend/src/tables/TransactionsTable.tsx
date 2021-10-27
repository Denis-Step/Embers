import React, {useMemo} from 'react';
import {useTable} from "react-table";
import {Transaction} from "../common/types";


export const useTransactionsTable = (transactions: Partial<Transaction>[]) => {

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

export const TransactionsTable = () => {
    const transactions = [
        {description: "Test",
            date: "2021-01-01",
            "amount": 5.00,
            merchantName: "Merchant Name",
            "transactionId": "transactionId"},
        {"description": "Test",
            "date": "2021-01-01",
            "amount": 5.00,
            "merchantName": "Merchant Name",
            "transactionId": "transactionId"}
    ]

    const {
        getTableProps,
        getTableBodyProps,
        headerGroups,
        rows,
        prepareRow,
        // eslint-disable-next-line react-hooks/rules-of-hooks
    } = useTransactionsTable(transactions)



    return (
        <table {...getTableProps()} style={{ border: 'solid 1px blue' }}>
            <thead>
            {headerGroups.map(headerGroup => (
                <tr {...headerGroup.getHeaderGroupProps()}>
                    {headerGroup.headers.map(column => (
                        <th
                            {...column.getHeaderProps()}
                            style={{
                                borderBottom: 'solid 3px red',
                                background: 'aliceblue',
                                color: 'black',
                                fontWeight: 'bold',
                            }}
                        >
                            {column.render('Header')}
                        </th>
                    ))}
                </tr>
            ))}
            </thead>
            <tbody {...getTableBodyProps()}>
            {rows.map(row => {
                prepareRow(row)
                return (
                    <tr {...row.getRowProps()}>
                        {row.cells.map(cell => {
                            return (
                                <td
                                    {...cell.getCellProps()}
                                    style={{
                                        padding: '10px',
                                        border: 'solid 1px gray',
                                    }}
                                >
                                    {cell.value}
                                </td>
                            )
                        })}
                    </tr>
                )
            })}
            </tbody>
        </table>
    )
}

