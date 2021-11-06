import React from 'react';
import {
    Switch,
    Route,
} from "react-router-dom";
import {MainRoutes} from "./MainRoutes";
import {AuthRedirect} from "./components/auth/AuthRedirect";

export const AuthCheck = () => {

    return (
            <Switch>
                <Route path="/googlelogin">
                    <AuthRedirect />
                </Route>
                <Route path="/">
                    <MainRoutes />
                </Route>
            </Switch>

    )
}