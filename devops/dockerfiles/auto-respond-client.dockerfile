FROM buildenv

WORKDIR /root
ADD tools/vcx-client-auto-respond.py .
ADD tools/team1.txn .
ADD tools/requirements.txt .
RUN pip3 install -r requirements.txt

RUN chmod u+x vcx-client-auto-respond.py
ENTRYPOINT ["./vcx-client-auto-respond.py"]

EXPOSE 4002
