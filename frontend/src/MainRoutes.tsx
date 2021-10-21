import React from 'react';
import {
    Switch,
    Route,
} from "react-router-dom";
import LinkAccount from "./plaid/LinkAccount";
import {CognitoAuthContextProvider} from "./contexts/cognitoAuthContext";
import {HomePage} from "./pages/HomePage";
import {TransactionsPage} from "./pages/TransactionsPage";

export const MainRoutes = () => {

    return (
        <CognitoAuthContextProvider>
            <Switch>
                <Route path="/link">
                    <LinkAccount />
                </Route>
                <Route path="/transactions/view">
                    <TransactionsPage />
                </Route>
                <Route path="/">
                    <HomePage />
                </Route>
            </Switch>
        </CognitoAuthContextProvider>

    )
}