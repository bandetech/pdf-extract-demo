import React from 'react';
import { useState, useEffect } from 'react';
import { Card, ListGroup, Tabs, Tab } from 'react-bootstrap';
import { apiKey } from '../googleApiKey';


export const QAs = ({qaList}) => {
    
    const lbToBr = (txt) =>{
        return (
          txt.split(/(\n)/g).map(t => (t === '\n')?<br/>:t)
        )
    };
    //console.log("qaList :", qaList);
    
    const Translation = ({q}) =>{
        console.log("Question arg:", q);
        const [loading, setLoading] = useState(false);
        const [translated, setTranslated] = useState("");
        const url = "https://translation.googleapis.com/language/translate/v2?key="+apiKey+"&q="+encodeURI(q)+"&source=ja&target=en";
        useEffect(()=>{
            setLoading(true);
            fetch(url)
            .then(res => res.json())
            .then((response) => {
                setLoading(false);
                console.log("google resp :", response);
                 
                setTranslated(response.data.translations[0].translatedText);
            })
        },[url]);
        if(loading){
            return <p>translating....</p>
        }
        return(
            <p>{translated}</p>
        );
    };

    const TranslationCn = ({q}) =>{
        console.log("Question arg:", q);
        const [loading, setLoading] = useState(false);
        const [translated, setTranslated] = useState("");
        const url = "https://translation.googleapis.com/language/translate/v2?key="+apiKey+"&q="+encodeURI(q)+"&source=ja&target=zh";
        useEffect(()=>{
            setLoading(true);
            fetch(url)
            .then(res => res.json())
            .then((response) => {
                setLoading(false);
                console.log("google resp :", response);
                 
                setTranslated(response.data.translations[0].translatedText);
            })
        },[url]);
        if(loading){
            return <p>translating....</p>
        }
        return(
            <p>{translated}</p>
        );
    };

    return(
        qaList.correspondences.map((qa, i) =>(
            <>
            <Tabs id="qanda-tabs" key={i}>
                <Tab eventKey="native" title="Native">
                    <Card>
                        <Card.Header>{lbToBr(qa.question)}</Card.Header>
                        <ListGroup variant="flash">
                            <ListGroup.Item>{lbToBr(qa.answer)}</ListGroup.Item>
                        </ListGroup>
                    </Card>
                </Tab>
                <Tab eventKey="english" title="English">
                     <Card>
                        <Card.Header><Translation q={qa.question}/></Card.Header>
                        <ListGroup variant="flash">
                            <ListGroup.Item><Translation q={qa.answer}/></ListGroup.Item>
                        </ListGroup>
                    </Card>
                </Tab>
                <Tab eventKey="simplifyCn" title="Simplify Chinese">
                    <Card>
                        <Card.Header><TranslationCn q={qa.question}/></Card.Header>
                        <ListGroup variant="flash">
                            <ListGroup.Item><TranslationCn q={qa.answer}/></ListGroup.Item>
                        </ListGroup>
                    </Card>
                </Tab>
            </Tabs>
            <p></p>
            </>
          ))
    );
}


