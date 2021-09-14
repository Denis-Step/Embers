// Redirect here.
import React from "react";
import {CognitoJwt} from "../common/types";
import {COGNITO_UI_URI} from "../common/constants";
import {AuthContext, CognitoAuthContextProvider} from "../contexts/cognitoAuthContext";
import { Spinner } from "@chakra-ui/react";


const getFromLocalStorage = (): CognitoJwt | null => {
    let authToken: any = {}
    authToken.access_token = localStorage.getItem("access_token");
    authToken.id_token = localStorage.getItem("id_token");
    authToken.expires_in = localStorage.getItem("expires_in");
    authToken.token_type = localStorage.getItem("token_type");

    // Put validation here
    if (!authToken.id_token) {
        return null;
    }

    return authToken;
}

export const AuthCheck = (props: { children?: React.ReactNode }) => {
    const localToken = getFromLocalStorage();

    // Redirect if no local token.
    if (!localToken) {
        window.location.replace(COGNITO_UI_URI);
        return <Spinner/>
    } else {
        return <CognitoAuthContextProvider token={localToken}>
            {props.children}
        </CognitoAuthContextProvider>
    }
}


