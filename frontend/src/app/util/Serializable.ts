export class Serializable {

  fillFromJSON(json: string) {

    let jsonObj = JSON.parse(json);

    for (const propName in jsonObj) {
      this[propName] = jsonObj[propName];
    }
  }
}
