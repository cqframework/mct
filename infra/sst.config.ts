import { SSTConfig } from "sst";
import { App } from "./stacks/MyStack";

export default {
  config(_input) {
    return {
      name: "infra",
      region: "us-east-1",
    };
  },
  stacks(app) {
    app.stack(App);
  },
} satisfies SSTConfig;
