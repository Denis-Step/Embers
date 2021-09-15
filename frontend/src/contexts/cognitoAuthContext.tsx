import React, {useEffect, useState} from "react";
import {Spinner} from "@chakra-ui/react";
import {CognitoJwt} from "../common/types";
import {COGNITO_UI_URI} from "../common/constants";
import {getFromLocalStorage, queueTokenFlush} from "../common/utils";


interface AuthContextValue {
    token: CognitoJwt;
    setToken: (token: CognitoJwt) => void;
}

interface CognitoAuthContextProviderProps {
    children?: React.ReactNode;
}

export const AuthContext = React.createContext<AuthContextValue | null>(null);

// Redirect here.
export const CognitoAuthContextProvider = (props: CognitoAuthContextProviderProps) => {

    // undefined at first, null if not found, CognitoJwt if valid.
    // BE CAREFUL EXTENDING setAuthToken!!!
    const [authToken, setAuthToken] = useState<CognitoJwt | null>();

    useEffect(() => {
        // Prevent infinite loop.
        if (!authToken) {
            const cognitoAuthToken = getFromLocalStorage();

            // Queue flush for when token expires.
            if (cognitoAuthToken) {
                const millisecondsLeft = Math.abs(Date.now() - cognitoAuthToken.expires_at.getTime());
                queueTokenFlush(millisecondsLeft, () =>  setAuthToken(null))
            }

            setAuthToken(cognitoAuthToken)
        }
    }, [authToken])

    if (!authToken) {
        if (authToken === null) {
            // Fire side effect to redirect to Cognito UI.
            window.location.replace(COGNITO_UI_URI);
        }
        // Return spinner while checking localStorage or redirecting.
        return <Spinner />
    }
    else {

        return <AuthContext.Provider value={{
            token: authToken,
            setToken: setAuthToken
        }}>
            {props.children}
        </AuthContext.Provider>
    }

}

// Wrap to avoid null contexts being passed down
// with annoying type checks.
export function useAuth() {
    const context = React.useContext(AuthContext)
    if (context === null) {
        throw new Error('useCount must be used within a CountProvider')
    }
    return context
}
