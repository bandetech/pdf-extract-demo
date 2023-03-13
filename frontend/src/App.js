import React from 'react';
import { useDropzone } from 'react-dropzone';
import { usePreviewPdf } from './hooks/usePreviewPdf';
import { Container, Row, Col, Spinner, Button } from 'react-bootstrap';
import { Attachments } from './components/Attachments';
import { QAs } from './components/QAs';
import logo from './img/small-logo.png';

import 'bootstrap/dist/css/bootstrap.min.css';
import './App.css';


const App = () => {

  const {qaList, isLoaded, isExtracting, onDrop, onClickForExtract} = usePreviewPdf();
  const {getRootProps, getInputProps} = useDropzone({onDrop});
     
  console.log("is loaded (app): ", isLoaded);
  console.log("is extracting :", isExtracting);

  return (
    <Container className="App">
      <Row className="App-header">
        <Col><img src={logo} alt="Adobe Logo"/> Adobe PDF Embed API and Extract API Demo</Col>
      </Row>
      <Row>
          <Col {...getRootProps({className: 'dropzone'})}>
            <input {...getInputProps()} />
            <p>PDFファイルをこちらにドロップ（又はここをクリック）</p>
          </Col>
      </Row>

      <Row>
        <Col id="left-pane" md={7}>
          <div id="pdf-div"></div>
          <div id="extract-button"><Button variant="primary" size="lg" onClick={onClickForExtract} style={{display: !isLoaded? 'none' : ''}}>Extract</Button></div>
        </Col>
        <Col id="right-pane" md={5}>
        {
            isExtracting ? <div><Spinner animation="grow" size="sm" />Analyzing...</div> : <QAs qaList={qaList}></QAs>
        }
        <Attachments attachments={qaList.attachments}/>
        </Col>
      </Row>
    </Container>
  );
}

export default App;
