import { Card, ListGroup } from 'react-bootstrap';

export const Attachments = props =>{

    const {attachments} = props;
    console.log(attachments);
    return(
        attachments.map((attachment, i) =>(
            <Card key={i}>
                <ListGroup.Item><a href={'http://localhost:8080/attachment?fileName=' + attachment}>Table</a></ListGroup.Item>
            </Card>
          ))
    );
}