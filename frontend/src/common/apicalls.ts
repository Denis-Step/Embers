import {BETA_ENDPOINT, ITEM_API_RESOURCE, LINK_API_RESOURCE, LINK_DEFAULT_PRODUCTS} from "./constants";
import axios from "axios";

// Get link token for plaid flow.
export const getLinkToken = async (user: string,
                             products?: string[]): Promise<string> => {

    // Pass in default list of products if link is not passed.
    products = (typeof products !== 'undefined') ? products : LINK_DEFAULT_PRODUCTS;
    const endpoint = BETA_ENDPOINT + LINK_API_RESOURCE;

    const request = await axios.post(endpoint,
        {user: user, products: products});

    return request.data;
}

export const postPublicToken = async (user: string, publicToken: string): Promise<String> => {
    const endpoint = BETA_ENDPOINT + ITEM_API_RESOURCE;
    const request = await axios.post(endpoint,
        {user: user, publicToken: publicToken})

    return request.data;
}