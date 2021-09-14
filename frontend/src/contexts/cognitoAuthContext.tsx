import React, {createContext, useEffect, useState} from "react";
import {CognitoJwt} from "../common/types";
import {COGNITO_UI_URI} from "../common/constants";
import {Spinner} from "@chakra-ui/react";

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


interface AuthContextValue {
    token: CognitoJwt | undefined;
    setToken: React.Dispatch<React.SetStateAction<CognitoJwt | undefined>>
}

interface CognitoAuthContextProviderProps {
    children?: React.ReactNode;
}

export const AuthContext = React.createContext<AuthContextValue | {}>({});

// Redirect here.
export const CognitoAuthContextProvider = (props: CognitoAuthContextProviderProps) => {
    const [authToken, setAuthToken] = useState<CognitoJwt | undefined>();
    const localToken = getFromLocalStorage();

    useEffect(() => {

        if (!localToken) {
            window.location.replace(COGNITO_UI_URI);
        } else {
            console.log(localToken)
            setAuthToken(localToken);
        }
    }, [])


    return <AuthContext.Provider value={{
        token: authToken,
        setToken: setAuthToken
    }}>
        {props.children}
    </AuthContext.Provider>

}
