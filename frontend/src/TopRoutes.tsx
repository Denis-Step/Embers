import React from 'react';
import {
    Switch,
    Route,
} from "react-router-dom";
import LinkAccount from "./plaid/LinkAccount";
import {AuthRedirect} from "./auth/AuthRedirect";
import {AuthCheck} from "./auth/AuthCheck";

export const TopRoutes = () => {

    return (
        <AuthCheck>
            <Switch>
                <Route path="/googlelogin">
                    <AuthRedirect />
                </Route>
                <Route path="/">
                    <LinkAccount />
                </Route>
            </Switch>
        </AuthCheck>

    )
}