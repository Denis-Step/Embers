import React from 'react';
import { ChakraProvider } from "@chakra-ui/react"
import {BrowserRouter} from "react-router-dom";
import {AuthCheck} from "./AuthCheck";

function App() {
  return (
      <ChakraProvider>
        <div className="App">
            <BrowserRouter>
                <AuthCheck />
            </BrowserRouter>
        </div>
      </ChakraProvider>
  );
}

export default App;
