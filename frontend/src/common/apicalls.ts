import {LINK_API_ENDPOINT, LINK_DEFAULT_PRODUCTS} from "./constants";
import axios from "axios";

// Get link token for plaid flow.
export const getLinkToken = async (user: string,
                             products?: string[]): Promise<string> => {

    // Pass in default list of products if link is not passed.
    products = (typeof products !== 'undefined') ? products : LINK_DEFAULT_PRODUCTS;

    const request = await axios.post(LINK_API_ENDPOINT,
        {user: user, products: products},
        {headers: {
            'Access-Control-Allow-Origin': '*',
                'Content-Type': 'application/json;charset=utf-8'
            }
        });

    return request.data;
}