import axios from "axios";
import {BETA_ENDPOINT, ITEM_API_RESOURCE, LINK_API_RESOURCE, LINK_DEFAULT_PRODUCTS} from "./constants";
import {PlaidItemCreationRequest} from "./types";

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

// To be called after LinkFlow completion.
export const requestItemCreation = async (itemInfo: PlaidItemCreationRequest): Promise<String> => {
    const endpoint = BETA_ENDPOINT + ITEM_API_RESOURCE;
    const request = await axios.post(endpoint,
        itemInfo)

    return request.data;
}