import React from 'react';
import {GoogleLogin, GoogleLoginResponse, GoogleLoginResponseOffline} from "react-google-login";
import {GOOGLE_AUTH_CLIENT_ID} from "../common/constants";
import {getIamCredentials} from "../common/apicalls";
import {CognitoJwt} from "../common/types";

const responseGoogle = (response: any) => {
    console.log(response);
    if (response.tokenId) {
        console.log(response.tokenId)
        const config = getIamCredentials(response.tokenId);
        console.log(config.secretAccessKey);
        console.log(config.accessKeyId);
    }
}

const decodeJwt = (): CognitoJwt => {
    // Remove pound sign.
    const rawParams = window.location.hash.substring(1)
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

    if (window.location.hash.includes("access_token")) {
        console.log(decodeJwt());
    }

    return (   <GoogleLogin
            clientId={GOOGLE_AUTH_CLIENT_ID}
            buttonText="Login"
            onSuccess={responseGoogle}
            onFailure={responseGoogle}
            cookiePolicy={'single_host_origin'}
        />
    );
}
