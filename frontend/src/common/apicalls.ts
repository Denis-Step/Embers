import axios from "axios";
import {config, CognitoIdentityCredentials} from "aws-sdk";
import {BETA_ENDPOINT, ITEM_API_RESOURCE, LINK_API_RESOURCE, LINK_DEFAULT_PRODUCTS, IDENTITY_POOL_ID} from "./constants";
import {PlaidItemCreationInfo} from "./types";

export const getIamCredentials = (token: string) => {
    config.credentials = new CognitoIdentityCredentials({
        IdentityPoolId: IDENTITY_POOL_ID,
        Logins: { // optional tokens, used for authenticated login
            'accounts.google.com': token
        }
    });

    return config.credentials;
}

// Get link token for plaid flow.
export const getLinkToken = async (user: string,
                             token: string,
                             webhook: boolean,
                             products?: string[]): Promise<string> => {

    // Pass in default list of products if link is not passed.
    products = (typeof products !== 'undefined') ? products : LINK_DEFAULT_PRODUCTS;
    const endpoint = BETA_ENDPOINT + LINK_API_RESOURCE;

    const request = await axios.post(endpoint,
        { user, products, webhook },
        {headers: {
            Authorization: token
            }});

    return request.data;
}

// To be called after LinkFlow completion.
export const requestItemCreation = async (itemInfo: PlaidItemCreationInfo, token: string): Promise<String> => {
    const endpoint = BETA_ENDPOINT + ITEM_API_RESOURCE;
    const request = await axios.post(endpoint,
        itemInfo, {
        headers: {
            Authorization: token
        }})

    return request.data;
}