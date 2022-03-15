import React from "react";
import DatePicker from 'react-datepicker';
import {Text} from '@chakra-ui/react';
import {ViewTransactionsSearchParams} from "../transactions/ViewTransactions";

export const ViewTransactionsSearchBox = (props: ViewTransactionsSearchBoxProps) => {

    const handleDateChange = (newDate: Date) => {
        const newParams = {...props.paramsState, startDate: newDate}
        props.handleChange(newParams);
    }

    return (
        <>
            <Text fontSize='24px' color='teal' >
                Start Date
            </Text>
            <DatePicker
                selected={props.paramsState.startDate}
                onChange={handleDateChange}
            />
        </>
    );

}

export interface ViewTransactionsSearchBoxProps {
    paramsState: ViewTransactionsSearchParams
    handleChange: (newState: ViewTransactionsSearchParams) => void
}
