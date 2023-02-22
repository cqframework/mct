import demoData from '../fixtures/MeasureReport.json';
import mctPopulationPayloadData from '../fixtures/MeasureReportMCTPopulation.json';
import mctIndividualPayloadData from '../fixtures/MeasureReportMCTIndividual.json';
import {
  extractDescription,
  processMeasureReportPayload,
  summarizeMeasureReport,
  populationGather,
  parseStratifier
} from './measureReportHelpers';

describe('measureReportHelpers', () => {
  describe('extractDescription', () => {
    it('should extract description from extension', () => {
      const description = extractDescription(demoData);
      expect(description).toEqual(
        'Percentage of patients 18-75 years of age with diabetes who had hemoglobin A1c > 9.0% during the measurement period'
      );
    });
  });

  describe('processMeasureReportPayload', () => {
    it('should process measure report payload', () => {
      const processedMeasureReport = processMeasureReportPayload(mctPopulationPayloadData);
      expect(processedMeasureReport).toMatchSnapshot();
    });

    it('should process measure report payload with individual level data', () => {
      const processedMeasureReport = processMeasureReportPayload(mctIndividualPayloadData);
      expect(processedMeasureReport).toMatchSnapshot();
    });
  });

  describe('summarizeMeasureReport', () => {
    it('should summarize measure report', () => {
      const summary = summarizeMeasureReport(mctPopulationPayloadData);
      expect(summary).toMatchSnapshot();
    });

    it('should summarize measure report with individual level data', () => {
      const summary = summarizeMeasureReport(mctIndividualPayloadData);
      expect(summary).toMatchSnapshot();
    });
  });

  describe('populationGather', () => {
    it('should gather population data', () => {
      const populationData = populationGather(demoData.group[0]);
      expect(populationData).toMatchSnapshot();
    });
  });

  describe('parseStratifier', () => {
    it('should parse stratifier', () => {
      const stratifier = parseStratifier(demoData);
      expect(stratifier).toMatchSnapshot();
    });
  });
});
