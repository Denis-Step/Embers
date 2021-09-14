import React, {useContext, useEffect} from 'react';
import {Spinner} from "@chakra-ui/react";
import {useLocation, useRouteMatch} from "react-router";
import {getIamCredentials} from "../common/apicalls";
import {CognitoJwt} from "../common/types";
import {AuthContext} from "../contexts/cognitoAuthContext";

const responseGoogle = (response: CognitoJwt) => {
    console.log(response);
    if (response.id_token) {
        console.log(response.id_token)
        const config = getIamCredentials(response.id_token);
        console.log(config.secretAccessKey);
        console.log(config.accessKeyId);
    }
}

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

export const JPGoogleLogin = () => {
    const routeMatch = useLocation();

    useEffect(() => {

        console.log("hash:", routeMatch.hash);
        const decodedJwt = decodeJwt(routeMatch.hash);
        console.log(decodedJwt);
        responseGoogle(decodedJwt);

    }, [routeMatch] )

    return (
        <Spinner />
    );
}
