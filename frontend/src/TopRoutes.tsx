import React from 'react';
import {
    Switch,
    Route,
} from "react-router-dom";
import LinkAccount from "./plaid/LinkAccount";
import {JPGoogleLogin} from "./auth/JPGoogleLogin";

export const TopRoutes = () => {

    return (
        <Switch>
            <Route path="/googlelogin">
                <JPGoogleLogin />
            </Route>
            <Route path="/">
                <LinkAccount />
            </Route>
        </Switch>

    )
}