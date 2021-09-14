import React, {useEffect} from 'react';
import {GoogleLogin, GoogleLoginResponse, GoogleLoginResponseOffline} from "react-google-login";
import {useLocation, useRouteMatch} from "react-router";
import {GOOGLE_AUTH_CLIENT_ID} from "../common/constants";
import {getIamCredentials} from "../common/apicalls";
import {CognitoJwt} from "../common/types";

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

    if (window.location.hash.includes("access_token")) {
        console.log(window.location.hash);
    }

    return (
        <GoogleLogin
            clientId={GOOGLE_AUTH_CLIENT_ID}
            buttonText="Login"
            onFailure={responseGoogle}
            cookiePolicy={'single_host_origin'}
        />
    );
}
