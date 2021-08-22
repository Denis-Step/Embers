import React, {useState, useCallback, useMemo} from 'react';
import { Button, FormControl, FormLabel, Input, FormHelperText } from "@chakra-ui/react"
import {PlaidLinkOnSuccessMetadata} from "react-plaid-link";
import {getLinkToken} from "../common/apicalls";
import LinkFlow from "./LinkFlow";

/* {"user":  "BillyBobThornton",
  "publicToken":  "public-development-32d715cf-252e-44cb-a230-95267d9e85fa",
  "institutionId":  "6969",
  "availableProducts" :  ["transactions"],
  "dateCreated":  "2021-08-21T22:05:05",
  "metaData":  {"foo":  "bar"}
} */

interface PlaidItem {}

export interface ItemCreationState {
    user: string;
    linkToken: string;
    publicToken: string;
}

function reducer(state, action) {

}

const LinkAccount = () => {
    // State for getLinkToken params.
    const [user, setUser] = useState<string>("");
    const [linkToken, setLinkToken] = useState<string>("");
    const [publicToken, setPublicToken] = useState<string>();
    const [metadata, setMetadata] = useState<object>();

    // Handler to set user.
    const updateUser = (event: React.FormEvent<HTMLInputElement>): void => {
        const input = event.currentTarget.value;
        setUser(input)
    };

    // Handler for link token button.
    const updateLinkToken = useCallback(async () => {
        if (user) {
            const link = await getLinkToken(user);
            setLinkToken(link);
        }
    }, [user]);

    // onSuccess callback for LinkFlow.
    const onLinkSuccess = useCallback(async (public_token: string,
                                           metadata: PlaidLinkOnSuccessMetadata) => {
        setPublicToken(public_token);
        setMetadata(metadata);
        // Send info back to server.
    },[])

    const linkFlow = useMemo(() => {
        // Fire off link flow iff there is a linkToken and no publicToken yet.
        if (linkToken && !Boolean(publicToken)) {
            return (<LinkFlow link_token={linkToken}
                      onSuccess={onLinkSuccess} />)
        } else {
            return null;
        }
        },
        [linkToken, publicToken, onLinkSuccess])


    return (
        <div id = "link-token-creation">
            <FormControl id="Link Params">
                <FormLabel>Request Link Token</FormLabel>
                <Input key="linkInput" type="user" value={user} placeholder="John" onChange={updateUser}  />
                <FormHelperText>Username for Plaid.</FormHelperText>
            </FormControl>
            <Button colorScheme="teal"
                    size="md"
                    onClick={updateLinkToken} >
                Get Link Token
            </Button>
            {linkFlow}
        </div>
    )

}

export default LinkAccount;
