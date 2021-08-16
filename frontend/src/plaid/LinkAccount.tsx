import React, {useState, useCallback} from 'react';
import { Button, FormControl, FormLabel, Input, FormHelperText } from "@chakra-ui/react"
import {getLinkToken} from "../common/apicalls";

const LinkAccount = () => {
    // State for getLinkToken params.
    const [user, setUser] = useState<string>("");
    const [linkToken, setLinkToken] = useState<string>();

    const updateUser = (event: React.FormEvent<HTMLInputElement>): void => {
        const input = event.currentTarget.value;
        setUser(input)
    };

    const updateLink = useCallback(async () => {
        if (user) {
            const link = await getLinkToken(user);
            setLinkToken(link);
        }
    }, [user]);

    const LinkTokenForm = (): JSX.Element => {
        return (
            <FormControl id="Link Params">
                <FormLabel>Request Link Token</FormLabel>
                <Input key="linkInput" type="user" value={user} placeholder="John" onChange={updateUser}  />
                <FormHelperText>Username for Plaid.</FormHelperText>
        </FormControl>
        )
    }

    const LinkTokenButton = (): JSX.Element => {
        return (
            <Button colorScheme="teal"
                    size="md"
                    onClick={updateLink}
            >
                Get Link Token
            </Button>
        )
    }

    return (
        <div id = "link-token-creation">
            <FormControl id="Link Params">
                <FormLabel>Request Link Token</FormLabel>
                <Input key="linkInput" type="user" value={user} placeholder="John" onChange={updateUser}  />
                <FormHelperText>Username for Plaid.</FormHelperText>
            </FormControl>
            <Button colorScheme="teal"
                    size="md"
                    onClick={updateLink} >
                Get Link Token
            </Button>
        </div>
    )

}

export default LinkAccount;
