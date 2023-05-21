FROM node:alpine
WORKDIR /usr/app/src
COPY Backend/package*.json ./
RUN npm install
COPY Backend/src .
CMD ["node", "index.js"]