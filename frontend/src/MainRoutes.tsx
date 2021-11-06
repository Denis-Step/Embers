import React from 'react';
import {
    Switch,
    Route,
} from "react-router-dom";
import LinkAccount from "./components/plaid/LinkAccount";
import {CognitoAuthContextProvider} from "./contexts/cognitoAuthContext";
import {HomePage} from "./pages/HomePage";
import {LinkAccountPage} from "./pages/LinkAccountPage";
import {ViewTransactionsPage} from "./pages/ViewTransactionsPage";

export const MainRoutes = () => {

    return (
        <CognitoAuthContextProvider>
            <Switch>
                <Route path="/link">
                    <LinkAccountPage />
                </Route>
                <Route path="/transactions/view">
                    <ViewTransactionsPage />
                </Route>
                <Route path="/">
                    <HomePage />
                </Route>
            </Switch>
        </CognitoAuthContextProvider>

    )
}