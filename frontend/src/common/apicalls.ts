import axios from "axios";
import {
    BETA_ENDPOINT,
    ITEM_API_RESOURCE,
    LINK_API_RESOURCE,
    LINK_DEFAULT_PRODUCTS,
    TRANSACTIONS_API_RESOURCE
} from "./constants";
import {PlaidItemCreationInfo, Transaction} from "./types";
import {formatDate} from "./utils";

// Get link token for clients.plaid flow.
export const getLinkToken = async (token: string,
                             webhook: boolean,
                             products?: string[]): Promise<string> => {

    // Pass in default list of products if link is not passed.
    products = (typeof products !== 'undefined') ? products : LINK_DEFAULT_PRODUCTS;
    const endpoint = BETA_ENDPOINT + LINK_API_RESOURCE;

    const request = await axios.post(endpoint,
        { products, webhook },
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

export const getTransactions = async (startDate: Date, token: string): Promise<Transaction[]> => {
    const endpoint = BETA_ENDPOINT + TRANSACTIONS_API_RESOURCE;
    const request = await axios.get(endpoint, {
        params: {
            startDate: formatDate(startDate)
    },
        headers: {
            Authorization: token
        }
    })

    return JSON.parse(request.data);
}

