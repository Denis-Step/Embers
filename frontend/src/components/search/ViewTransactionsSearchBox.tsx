import React from "react";
import {Calendar, CalendarChangeParams} from "primereact/calendar";
import {ViewTransactionsSearchParams} from "../transactions/ViewTransactions";

export const ViewTransactionsSearchBox = (props: ViewTransactionsSearchBoxProps) => {

    const handleDateChange = (e: CalendarChangeParams) => {
        const newDate = e.target.value as Date;

        const newParams = {...props.paramsState, startDate: newDate}
        props.handleChange(newParams);
    }

    return (
        <Calendar dateFormat="yy/mm/dd"
                  value={props.paramsState.startDate}
                  onChange={handleDateChange} />
    );

}

export interface ViewTransactionsSearchBoxProps {
    paramsState: ViewTransactionsSearchParams
    handleChange: (newState: ViewTransactionsSearchParams) => void
}
