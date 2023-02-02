import { createPeriodFromQuarter } from './queryHelper'

describe('queryHelper', () => {
  describe('createPeriodFromQuarter', () => {
    afterEach(() => {
      jest.useRealTimers()
    })

    it('should return date range for q1', () => {
      jest.useFakeTimers("modern");
      jest.setSystemTime(new Date(1578958478000));
      const dateRange = createPeriodFromQuarter('q1')
      expect(dateRange.start).toEqual('01-01-2022')
    })
  })
  
})