import { useState } from "react";
import ViewSDKClient from "../ViewSDKClient";

export const usePreviewPdf = () =>{
    const [qaList, setQaList] = useState({correspondences:[], attachments:[]});
    const [incomingFile, setIncomingFile] = useState();
    const [isExtracting, setIsExtracting] = useState(false);
    const [isLoaded, setIsLoaded] = useState(false);
    const viewSDKClient = new ViewSDKClient();

    //console.log("Is Loaded :", isLoaded);
    const isValidPDF = (file) => {
        if(file.type === "application/pdf"){
          return true;
        }
        if(file.type === "" && file.name){
          const fileName = file.name;
          const lastDotIndex = fileName.lastIndexOf(".");
          if(lastDotIndex === -1 || fileName.substr(lastDotIndex).toUpperCase() !== "PDF") return false;
          return true;
        }
    
        return false;
      };
    
    
    const onDrop = (acceptedFiles) => {
        console.log("File was dropped :", acceptedFiles);
        setQaList({correspondences:[], attachments:[]});
        viewSDKClient.ready().then(() => {
          if(acceptedFiles.length > 0 && isValidPDF(acceptedFiles[0])){
            setIncomingFile(acceptedFiles[0]);
            const fileName = acceptedFiles[0].name;
            const reader = new FileReader();
            reader.onabort = () => console.log("file reading was aborted.");
            reader.onerror = () => console.log("file reading has failed");
            reader.onload = (e) => {
              const filePromise = Promise.resolve(e.target.result);
              viewSDKClient.previewFileUsingFilePromise("pdf-div", filePromise, fileName);
            };
            reader.readAsArrayBuffer(acceptedFiles[0]);
          }
        })
        .then(()=>{
          setIsLoaded(true);
        });
      };

    const onClickForExtract = () =>{
      setIsExtracting(true);
      console.log("onClickForExtract comes", incomingFile);
      const formData = new FormData();
      formData.append("file", incomingFile);
      const param = {
          method: "POST",
          body: formData
      }
      
      fetch("http://localhost:8080/extract", param)
        .then((res)=>{
            return res.json();
        })
        .then((json)=>{
            
            console.log(json.path);
            const encodedParam = encodeURIComponent(json.path);
            fetch(`http://localhost:8080/analyze?path=${encodedParam}`)
                .then((res)=>{
                    return res.json();
                })
                .then((json)=>{
                    console.log(json);
                    setQaList(json);
                })
                .catch((error)=>{
                    console.log("Error while requesting analyze ");
                })

        }).catch((error) => {
            console.log("ERROR While requesting extract ");
        }).finally(()=>{
          setIsExtracting(false);      
        });
    };

    
    return {qaList, isLoaded, isExtracting, onDrop, onClickForExtract};

}