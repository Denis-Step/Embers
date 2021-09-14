import React, { useState} from "react";
import {CognitoJwt} from "../common/types";


interface AuthContextValue {
    token: CognitoJwt | undefined;
    setToken: React.Dispatch<React.SetStateAction<CognitoJwt | undefined>>
}

interface CognitoAuthContextProviderProps {
    token: CognitoJwt;
    children?: React.ReactNode;
}

export const AuthContext = React.createContext<AuthContextValue | {}>({});

// Redirect here.
export const CognitoAuthContextProvider = (props: CognitoAuthContextProviderProps) => {
    const [authToken, setAuthToken] = useState<CognitoJwt>(props.token);

    return <AuthContext.Provider value={{
        token: authToken,
        setToken: setAuthToken
    }}>
        {props.children}
    </AuthContext.Provider>

}
