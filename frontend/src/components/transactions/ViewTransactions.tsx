import React, {useState} from "react";
import {useTransactions} from "../../common/hooks";
import {ViewTransactionsSearchBox} from "../search/ViewTransactionsSearchBox";
import {TransactionsTable} from "../tables/TransactionsTable";

export const ViewTransactions = () => {
    const [params, setParams] = useState<ViewTransactionsSearchParams>(initialParams());
    const transactions = useTransactions(params.startDate);

    return (
        <div>
            <ViewTransactionsSearchBox paramsState={params} handleChange={setParams} />
            <TransactionsTable transactions={transactions } />
        </div>
    )
}

export interface ViewTransactionsSearchParams {
    startDate: Date;
}

const initialParams = () => {
    const initialDate = new Date();
    const DEFAULT_DAYS_AGO = 60;
    const millisecondsInDay = 86400000 //number of milliseconds in a day

    return {
        startDate: new Date(initialDate.getTime() - (DEFAULT_DAYS_AGO * millisecondsInDay))
    }
}