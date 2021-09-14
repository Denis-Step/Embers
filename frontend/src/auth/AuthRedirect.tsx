import React, {createContext, useEffect, useState} from "react";
import {CognitoJwt} from "../common/types";
import {Redirect, useLocation} from "react-router";

// Use id_token property.
const decodeJwt = (urlHash: string): CognitoJwt => {
    // Remove pound sign.
    const rawParams = urlHash.substring(1)
    const paramsList = rawParams.split("&");

    let tokenParams: any = {};
    paramsList.forEach((param) => {
        const paramList = param.split("=");
        tokenParams[paramList[0]] = paramList[1];
    })

    // PERFORM SOME VALIDATION HERE.
    return tokenParams
}

const putInLocalStorage = (token: CognitoJwt): void => {
    localStorage.setItem("access_token", token.access_token);
    localStorage.setItem("id_token", token.id_token);
    localStorage.setItem("expires_in", token.expires_in);
    localStorage.setItem("token_type", token.token_type)
}


// Fire effect to store in local storage &
// Trigger Redirect here.
export const AuthRedirect = () => {
    const routeMatch = useLocation();

    useEffect(() => {

        if (routeMatch.hash) {
            console.log("hash:", routeMatch.hash);
            const decodedJwt = decodeJwt(routeMatch.hash);
            console.log(decodedJwt);
            putInLocalStorage(decodedJwt);
        }

    }, [routeMatch.hash])

    return <Redirect to={"/"} />
}