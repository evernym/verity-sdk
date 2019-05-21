import { Request, Response } from 'express-serve-static-core';

export async function initializeSSE(_REQ: Request, res: Response) {

    res.set({
        'Access-Control-Allow-Origin': '*',
        'Cache-Control': 'no-cache',
        'Connection': 'keep-alive',
        'Content-Type': 'text/event-stream',
    })
    
    res.write('retry: 10000\n\n')
}

export class ServerEvent {
    private data: string

    constructor() {
        this.data = ''
    }

    public addData(data: any) {
        this.data += JSON.stringify(data) + '\n'
    }

    public getEventData() {
        return this.data + '\n'
    }
}
