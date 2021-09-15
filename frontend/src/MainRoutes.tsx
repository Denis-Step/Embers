import React from 'react';
import {
    Switch,
    Route,
} from "react-router-dom";
import LinkAccount from "./plaid/LinkAccount";
import {CognitoAuthContextProvider} from "./contexts/cognitoAuthContext";

export const MainRoutes = () => {

    return (
        <CognitoAuthContextProvider>
            <Switch>
                <Route path="/">
                    <LinkAccount />
                </Route>
            </Switch>
        </CognitoAuthContextProvider>

    )
}