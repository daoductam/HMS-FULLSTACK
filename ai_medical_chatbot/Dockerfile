FROM python:3.11-slim

WORKDIR /app

# Install dependencies directly to bypass requirements.txt file requirement
RUN pip install --no-cache-dir fastapi uvicorn mysql-connector-python python-dotenv requests

COPY . /app

EXPOSE 8000

CMD ["uvicorn", "app:app", "--host", "0.0.0.0", "--port", "8000"]
