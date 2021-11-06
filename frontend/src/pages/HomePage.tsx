import {TopNavBar} from "../components/navigation/Nav";
import {
    Box,
    Flex,
    Text,
    IconButton,
    Button,
    Stack,
    Collapse,
    Icon,
    Link,
    Popover,
    PopoverTrigger,
    PopoverContent,
    useColorModeValue,
    useBreakpointValue,
    useDisclosure,
} from '@chakra-ui/react';
import {
    HamburgerIcon,
    CloseIcon,
    ChevronDownIcon,
    ChevronRightIcon,
} from '@chakra-ui/icons';
import {ViewTransactionsSearchBox} from "../components/search/ViewTransactionsSearchBox";
import {useTransactions} from "../common/hooks";
import React, {useState} from "react";

export const HomePage = () => {

    return (
        <>
            <TopNavBar />
        </>
    )

}
