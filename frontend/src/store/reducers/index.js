import { combineReducers } from 'redux';

import filter from './filter';
import data from './data';

const reducers = combineReducers({ filter, data });

export default reducers;
