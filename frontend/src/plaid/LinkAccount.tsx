import React, {useState, useCallback, useMemo, useReducer} from 'react';
import { Button, FormControl, FormLabel, Input, FormHelperText } from "@chakra-ui/react"
import {PlaidLinkOnSuccessMetadata} from "react-plaid-link";
import {getLinkToken, requestItemCreation} from "../common/apicalls";
import LinkFlow from "./LinkFlow";
import {PlaidItemCreationRequest} from "../common/types";

// @TODO: Use discriminated unions to type reducer actions.

enum ActionKind {
    UpdateUser = "UPDATEUSER",
    UpdateLinkToken = "UPDATELINKTOKEN",
    UpdatePublicToken = "UPDATEPUBLICTOKEN"
}

type Action = {
    type: ActionKind,
    payload: any
}

type State = {
    user?: string;
    linkToken?: string;
    publicToken?: string;
}

const initialState: State = {};

function linkReducer(state: Partial<PlaidItemCreationRequest>, action: Action): State {
    switch(action.type) {
        case ActionKind.UpdateUser:
            return { ...state, user: action.payload};
        case ActionKind.UpdateLinkToken:
            return {...state, linkToken: action.payload};
        case ActionKind.UpdatePublicToken:
            return {...state, publicToken: action.payload}
    }
}

const LinkAccount = () => {
    // State for getLinkToken params.
    const [user, setUser] = useState<string>("");
    const [linkToken, setLinkToken] = useState<string>("");
    const [publicToken, setPublicToken] = useState<string>();
    const [metadata, setMetadata] = useState<object>();
    const [state, dispatch] = useReducer(linkReducer, initialState);

    // Handler to set user.
    const updateUser = (event: React.FormEvent<HTMLInputElement>): void => {
        const input = event.currentTarget.value;
        dispatch({type: ActionKind.UpdateUser, payload: input});
    };

    // Handler for link token button.
    const updateLinkToken = useCallback(async () => {
        if (user) {
            const link = await getLinkToken(user);
            dispatch({type: ActionKind.UpdateLinkToken, payload: link});
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
