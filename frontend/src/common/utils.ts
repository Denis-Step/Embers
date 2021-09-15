import {CognitoJwt} from "./types";

// Use id_token property.
export const decodeJwt = (urlHash: string): CognitoJwt => {
    // Remove pound sign.
    const rawParams = urlHash.substring(1)
    const paramsList = rawParams.split("&");

    let tokenParams: any = {};
    paramsList.forEach((param) => {
        const paramList = param.split("=");
        tokenParams[paramList[0]] = paramList[1];
    })

    // Convert to milliseconds
    tokenParams['expires_at'] = Date.now() + tokenParams['expires_in'] * 1000

    // PERFORM SOME VALIDATION HERE.
    return tokenParams
}

export const putInLocalStorage = (token: CognitoJwt): void => {
    localStorage.setItem("access_token", token.access_token)
    localStorage.setItem("id_token", token.id_token)
    localStorage.setItem("expires_at", token.expires_at.toString())
    localStorage.setItem("token_type", token.token_type)
}

export const getFromLocalStorage = (): CognitoJwt | null => {
    let authToken: any = {}
    authToken.access_token = localStorage.getItem("access_token");
    authToken.id_token = localStorage.getItem("id_token");
    authToken.token_type = localStorage.getItem("token_type");

    const expirationDate = localStorage.getItem("expires_at");

    // Check these two fields only. Other two unused for now.
    if (!(authToken.id_token && expirationDate)) {
        return null;
    }

    authToken.expires_at = new Date(Number(expirationDate));
    if (Date.now() > authToken.expires_at) {
        flushFromLocalStorage();
        return null;
    }

    return authToken;
}

export const flushFromLocalStorage = (): void => {
    localStorage.removeItem("access_token");
    localStorage.removeItem("id_token");
    localStorage.removeItem("token_type");
    localStorage.removeItem("expires_at");
}

// Queue up deleting an expired token with callback parameter meant to be used
// With async context updates.
export const queueTokenFlush = (milliseconds: number, callback?: () => void ) => {

    const queuedFunction = (): void => {
        flushFromLocalStorage();
        if (callback) {
            callback();
        }
    }

    setTimeout( () => queuedFunction(), milliseconds )

}