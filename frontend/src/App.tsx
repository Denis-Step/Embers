import React from 'react';
import { ChakraProvider } from "@chakra-ui/react"
import {BrowserRouter} from "react-router-dom";
import {TopRoutes} from "./TopRoutes";

function App() {
  return (
      <ChakraProvider>
        <div className="App">
            <BrowserRouter>
                <TopRoutes />
            </BrowserRouter>
        </div>
      </ChakraProvider>
  );
}

export default App;
