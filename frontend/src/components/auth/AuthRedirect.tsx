import React, {useEffect, useState} from "react";
import {Redirect, useLocation} from "react-router";
import {decodeJwt, putInLocalStorage} from "../../common/utils";
import {Spinner} from "@chakra-ui/react";
import {CognitoJwt} from "../../common/types";

// Fire effect to store in local storage &
// Trigger Redirect here.
export const AuthRedirect = () => {
    const routeMatch = useLocation();
    const [authToken, setAuthToken] = useState<CognitoJwt>(); // Used to delay execution.

    useEffect(() => {

        if (routeMatch.hash) {
            console.log("hash:", routeMatch.hash);
            const decodedJwt = decodeJwt(routeMatch.hash);
            console.log(decodedJwt);

            if (!decodedJwt) {
                throw new Error(`You have arrived at this page by mistake or an error has occurred.
                                Please go back to the home page.`)
            }

            putInLocalStorage(decodedJwt);
            setAuthToken(decodedJwt);
        }

    }, [routeMatch.hash])

    if (authToken) {
        return <Redirect to={"/"}/>
    } else {
        return <Spinner />
    }
}