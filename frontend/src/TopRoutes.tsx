import React from 'react';
import {
    Switch,
    Route,
} from "react-router-dom";
import LinkAccount from "./plaid/LinkAccount";
import {CognitoAuthContextProvider} from "./contexts/cognitoAuthContext";
import {JPGoogleLogin} from "./auth/JPGoogleLogin";
import {AuthRedirect} from "./auth/AuthRedirect";

export const TopRoutes = () => {

    return (
        <CognitoAuthContextProvider>
            <Switch>
                <Route path="/googlelogin">
                    <AuthRedirect />
                </Route>
                <Route path="/">
                    <LinkAccount />
                </Route>
            </Switch>
        </CognitoAuthContextProvider>

    )
}